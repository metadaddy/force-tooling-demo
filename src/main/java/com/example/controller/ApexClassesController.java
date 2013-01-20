package com.example.controller;

import static com.github.ryanbrainard.richsobjects.filters.FilterRichSObjectsByFields.StringFieldsOnly;
import static com.github.ryanbrainard.richsobjects.filters.FilterRichSObjectsByFields.UpdateableFieldsOnly;

import java.io.IOException;
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
import com.github.ryanbrainard.richsobjects.RichSObject;

@Controller
@RequestMapping("/classes")
public class ApexClassesController {

	@RequestMapping("")
	public String listClasses(Map<String, Object> map) throws ServletException {
		try {
			JSONObject json = ToolingApi
					.get("query/?q=SELECT+Id,+Name+FROM+ApexClass+ORDER+BY+Name");
			map.put("records", json.get("records"));
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "classes";
	}

	@RequestMapping("/c")
	public String createClassDetail(Map<String, Object> map) {
		return "createClass";
	}

    @RequestMapping(method = RequestMethod.POST, value = "/c")
    public String updateContact(HttpServletRequest request, Map<String, Object> map) throws IOException {
        final ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        final Map<String,String> formData = new FormHttpMessageConverter().read(null, inputMessage).toSingleValueMap();
        final String name = formData.get("name");
		final String body = "public class "+name+" {\n\n}";
        
        try {
			JSONObject apexClass = ToolingApi
					.post("sobjects/ApexClass",
							"{\"Name\" : \"" + name + "\","
							+ "\"Body\" : \"" + JSONObject.escape(body)
							+ "\"}");
			System.out.println("ApexClass id: "
					+ apexClass.get("id"));

			return "redirect:" + apexClass.get("id");
        } catch (RuntimeException e) {
            map.put("error", e.getMessage()); // TODO: better looking error
            return "../";
        }
    }

	@RequestMapping("/{id}")
	public String getClassDetail(@PathVariable("id") String id,
			Map<String, Object> map) throws ServletException {
		try {
			JSONObject json = ToolingApi.get("sobjects/ApexClass/" + id);
			map.put("body", json.get("Body"));
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "classDetail";
	}

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
			JSONObject metadataContainer = ToolingApi.post(
					"sobjects/MetadataContainer", "{\"Name\":\"SaveClass" + id
							+ "\"}");
			System.out.println("MetadataContainer id: "
					+ metadataContainer.get("id"));

			JSONObject apexClassMember = ToolingApi
					.post("sobjects/ApexClassMember",
							"{\"MetadataContainerId\":\""
									+ metadataContainer.get("id") + "\","
									+ "\"ContentEntityId\" : \"" + id + "\","
									+ "\"Body\" : \"" + JSONObject.escape(body)
									+ "\"}");
			System.out.println("ApexClassMember id: "
					+ apexClassMember.get("id"));

			JSONObject containerAsyncRequest = ToolingApi.post(
					"sobjects/ContainerAsyncRequest",
					"{\"MetadataContainerId\":\"" + metadataContainer.get("id")
							+ "\", " + "\"isCheckOnly\" : false}");
			System.out.println("ContainerAsyncRequest id: "
					+ containerAsyncRequest.get("id"));

			JSONObject result = ToolingApi.get("sobjects/ContainerAsyncRequest/"
					+ containerAsyncRequest.get("id"));
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
						+ containerAsyncRequest.get("id"));
				state = (String) result.get("State");
				System.out.println("State: " + state);
			}

			ToolingApi.delete("sobjects/MetadataContainer/"
					+ metadataContainer.get("id"));

			if (state.equals("Completed")) {
				return "redirect:../classes";
			} else {
				map.put("body", body);
				map.put("errorMsg", result.get("ErrorMsg"));
				String compilerErrors = (String)result
						.get("CompilerErrors");
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
    public String deleteApexClass(@PathVariable("id") String id, Map<String, Object> map) throws ServletException {
		try {
			ToolingApi.delete("sobjects/ApexClass/" + id);
		} catch (IOException e) {
			throw new ServletException(e);
		}

        return "OK";
    }
}
