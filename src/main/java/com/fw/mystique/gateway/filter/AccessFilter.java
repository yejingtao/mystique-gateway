package com.fw.mystique.gateway.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.netflix.zuul.ZuulFilter;

public class AccessFilter extends ZuulFilter{
	
	public final Logger logger = LoggerFactory.getLogger(AccessFilter.class);
	
	public final static String HEADER_KEY = "Called-By";
	
	public final static String HEADER_VALUE = "mystique";
	
	@Value("${filter.accessIpList.filePath}")
	private String ipFilePath;

	public Object run() {
		/**
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String ip = getIpAddress(request);
		
		//IP白名单
		if(!validateIpAddress(ip)) {
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
			return null;
		}
		*/
		return null;
	}

	public boolean shouldFilter() {
		return true;	
	}

	@Override
	public int filterOrder() {
		//过滤等级默认0
		return 0;
	}

	@Override
	public String filterType() {
		//进入前过滤
		return "pre";
	}
	
	/**
	 * IP白名单验证
	 * @param ip
	 * @return
	 */
	private boolean validateIpAddress(String ip) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(ipFilePath), StandardCharsets.UTF_8);
			long ipNumber = lines.stream().filter((s) -> ip.equals(s)).count();
			if(ipNumber>0) {
				logger.info(String.format("Ip %s is in access list ", ip));
				return true;
			}else {
				logger.error(String.format("Ip %s is not in access list ", ip));
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("filter.accessIpList.filePath is not ok");
			return false;
		}		
	}
	
	private String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;

	}

}