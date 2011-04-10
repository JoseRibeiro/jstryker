package org.jstryker.domain;

import javax.annotation.Resource;

@Resource
public class Annotated {
	
	@Resource
	public void annotatedMethod() {
		
	}
	
	public void notAnnotatedMethod() {
		
	}
}
