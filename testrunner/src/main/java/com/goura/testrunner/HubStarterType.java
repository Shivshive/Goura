package com.goura.testrunner;

public enum HubStarterType{
	HUB ("hub"),
	Node ("node");
	
	 private final String name; 
	 private HubStarterType(String s) {
	        name = s;
	 }
	 
	 public boolean equalsName(String otherName) {		      
	        return name.equals(otherName);
	 }

	 public String toString() {
	       return this.name;
	 }
}