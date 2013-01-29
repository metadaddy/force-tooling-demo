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
@RequestMapping("/logs")
public class ApexLogsController {

	@RequestMapping("")
	public String listLogs(Map<String, Object> map) throws ServletException {
		String query = "SELECT Id, StartTime FROM ApexLog ORDER BY StartTime";
		try {
			JSONObject queryResponse = ToolingApi.get("query/?q="
					+ URLEncoder.encode(query, "UTF-8"));
			System.out.println("Got "
					+ ((JSONArray) queryResponse.get("records")).size()
					+ " logs");
			map.put("records", queryResponse.get("records"));
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "logs";
	}

	@RequestMapping("/{id}")
	public String getLogDetail(@PathVariable("id") String id,
			Map<String, Object> map) throws ServletException {
		try {
			String body = ToolingApi.getFile(id);
			map.put("body", body);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "logDetail";
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public String deleteApexLog(@PathVariable("id") String id,
			Map<String, Object> map) throws ServletException {
		try {
			ToolingApi.delete("sobjects/ApexLog/" + id);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return "OK";
	}
}
