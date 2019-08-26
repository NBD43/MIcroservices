package com.bridgelabz.fundoo.user.model;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class MailModel implements Serializable
{

private static final long serialVersionUID = 1L;
private String to;
private String from;
private String subject;
private String body;

public MailModel() {
super();
}

public MailModel(String to, String from, String subject, String body) {

this.to = to;
this.from = from;
this.subject = subject;
this.body = body;
}

public String getTo() {
return to;
}

public void setTo(String to) {
this.to = to;
}

public String getFrom() {
return from;
}

public void setFrom(String from) {
this.from = from;
}

public String getSubject() {
return subject;
}

public void setSubject(String subject) {
this.subject = subject;
}

public String getBody() {
return body;
}

public void setBody(String body) {
this.body = body;
}

@Override
public String toString() {
return "EmailId [to=" + to + ", from=" + from + ", subject=" + subject + ", body=" + body + "]";
}

}
