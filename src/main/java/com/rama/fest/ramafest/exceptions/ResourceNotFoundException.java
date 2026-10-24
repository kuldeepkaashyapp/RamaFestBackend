package com.rama.fest.ramafest.exceptions;


public class ResourceNotFoundException extends RuntimeException {
     String  resourceName;
     String  message;
   Long field;
    String  fieldName;

  public ResourceNotFoundException(String resourceName, String message,long field,String fieldName) {

   super(String.format("%s not found with %s:%d",resourceName,field,fieldName));
      this.resourceName=resourceName;
      this.message=message;
      this.field=field;
      this.fieldName=fieldName;

  }


  public ResourceNotFoundException(String resourceName, String message,Long field) {
      super(String.format("%s not found with %s:%d",resourceName,field,field));
      this.resourceName=resourceName;
      this.message=message;
      this.field=field;
  }


  public ResourceNotFoundException() {}


    public ResourceNotFoundException(String message) {
      this.message=message;
    }

}
