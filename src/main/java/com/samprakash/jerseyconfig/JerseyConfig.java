package com.samprakash.jerseyconfig;

import org.glassfish.jersey.server.ResourceConfig;

import com.samprakash.searchviewmodel.SearchRestApi;

import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {

		packages("com.samprakash.searchviewmodel");

		register(SearchRestApi.class);
	}
}
