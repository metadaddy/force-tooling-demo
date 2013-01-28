package com.example.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.util.ToolingApi;

@Controller
@RequestMapping("/classes")
public class ApexClassesController {

	@RequestMapping("")
	public String listClasses(Map<String, Object> map) throws ServletException {
		String query = "SELECT Id, Name FROM ApexClass ORDER BY Name";
		try {
			JSONObject queryResponse = ToolingApi.get("query/?q="
					+ URLEncoder.encode(query, "UTF-8"));
			System.out.println("Got "
					+ ((JSONArray) queryResponse.get("records")).size()
					+ " classes");
			map.put("records", queryResponse.get("records"));
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "classes";
	}

	@RequestMapping("/c")
	public String createClassDetail(Map<String, Object> map) {
		return "createClass";
	}

	// Simple JSON uses raw HashMap
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/c")
	public String createClass(HttpServletRequest request,
			Map<String, Object> map) throws IOException {
		final ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(
				request);
		final Map<String, String> formData = new FormHttpMessageConverter()
				.read(null, inputMessage).toSingleValueMap();
		final String name = formData.get("name");
		final String body = "public class " + name + " {\n\n}";

		try {
			JSONObject apexClassRequest = new JSONObject();
			apexClassRequest.put("Name", name);
			apexClassRequest.put("Body", body);
			JSONObject apexClassResponse = ToolingApi.post(
					"sobjects/ApexClass", apexClassRequest);
			System.out.println("ApexClass id: " + apexClassResponse.get("id"));

			return "redirect:" + apexClassResponse.get("id");
		} catch (RuntimeException e) {
			map.put("error", e.getMessage()); // TODO: better looking error
			return "../";
		}
	}

	@RequestMapping("/{id}")
	public String getClassDetail(@PathVariable("id") String id,
			Map<String, Object> map) throws ServletException {
		try {
			JSONObject apexClassResponse = ToolingApi.get("sobjects/ApexClass/"
					+ id);
			map.put("body", apexClassResponse.get("Body"));
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "classDetail";
	}

	// Simple JSON uses raw HashMap
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public String updateClassDetail(@PathVariable("id") String id,
			HttpServletRequest request, Map<String, Object> map)
			throws IOException {
		final ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(
				request);
		final Map<String, String> formData = new FormHttpMessageConverter()
				.read(null, inputMessage).toSingleValueMap();
		String body = formData.get("body");

		try {
			JSONObject metadataContainerRequest = new JSONObject();
			metadataContainerRequest.put("Name", "SaveClass" + id);
			JSONObject metadataContainerResponse = ToolingApi.post(
					"sobjects/MetadataContainer", metadataContainerRequest);
			System.out.println("MetadataContainer id: "
					+ metadataContainerResponse.get("id"));

			JSONObject apexClassMemberRequest = new JSONObject();
			apexClassMemberRequest.put("MetadataContainerId",
					metadataContainerResponse.get("id"));
			apexClassMemberRequest.put("ContentEntityId", id);
			apexClassMemberRequest.put("Body", body);
			JSONObject apexClassMemberResponse = ToolingApi.post(
					"sobjects/ApexClassMember", apexClassMemberRequest);
			System.out.println("ApexClassMember id: "
					+ apexClassMemberResponse.get("id"));

			JSONObject containerAsyncRequest = new JSONObject();
			containerAsyncRequest.put("MetadataContainerId",
					metadataContainerResponse.get("id"));
			containerAsyncRequest.put("isCheckOnly", false);
			JSONObject containerAsyncResponse = ToolingApi.post(
					"sobjects/ContainerAsyncRequest", containerAsyncRequest);
			System.out.println("ContainerAsyncRequest id: "
					+ containerAsyncResponse.get("id"));

			JSONObject result = ToolingApi
					.get("sobjects/ContainerAsyncRequest/"
							+ containerAsyncResponse.get("id"));
			String state = (String) result.get("State");
			System.out.println("State: " + state);
			int wait = 1;
			while (state.equals("Queued")) {
				try {
					System.out.println("Sleeping for " + wait + " second(s)");
					Thread.sleep(wait * 1000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

				wait *= 2;

				result = ToolingApi.get("sobjects/ContainerAsyncRequest/"
						+ containerAsyncResponse.get("id"));
				state = (String) result.get("State");
				System.out.println("State: " + state);
			}

			ToolingApi.delete("sobjects/MetadataContainer/"
					+ metadataContainerResponse.get("id"));

			if (state.equals("Completed")) {
				return "redirect:../classes";
			} else {
				map.put("body", body);
				map.put("errorMsg", result.get("ErrorMsg"));
				String compilerErrors = (String) result.get("CompilerErrors");
				if (compilerErrors != null) {
					JSONArray parsedErrors = (JSONArray) JSONValue
							.parse(compilerErrors);
					map.put("compilerErrors", parsedErrors);
				}
				return "classDetail";
			}
		} catch (RuntimeException e) {
			map.put("errorMsg", e.getMessage()); // TODO: better looking error
			return "classDetail";
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public String deleteApexClass(@PathVariable("id") String id,
			Map<String, Object> map) throws ServletException {
		try {
			ToolingApi.delete("sobjects/ApexClass/" + id);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "OK";
	}
}
