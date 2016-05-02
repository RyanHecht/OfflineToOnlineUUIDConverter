package net.ryanhecht.MCPHotel.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class getUUID {
	public static UUID get(String user) {
		UUIDFetcher u = new UUIDFetcher(Arrays.asList(user));
		Map<String, UUID> response = null;
		try {
		response = u.call();
		} catch (Exception e) {
		e.printStackTrace();
		}
		return response.get(user);
	}
	public static Map<String,UUID> get(List<String> user) {
		UUIDFetcher u = new UUIDFetcher(user);
		Map<String, UUID> response = null;
		try {
		response = u.call();
		} catch (Exception e) {
		e.printStackTrace();
		}
		return response;
	}
}
