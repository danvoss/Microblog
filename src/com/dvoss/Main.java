package com.dvoss;

import spark.ModelAndView;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static User user;
    static Message message;
    //static ArrayList<User> userList = new ArrayList<>();
    static ArrayList<Message> messageArrayList = new ArrayList<>();
    static HashMap<String, User> pwMap = new HashMap<>();

    public static void main(String[] args) {
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    HashMap m = new HashMap();
                    if (user == null) {
                        return new ModelAndView(m, "index.html");
                    }
                    else {
                        m.put("name", user.name);
                        m.put("messages", messageArrayList);
                        //m.put("password", user.password);
                        //m.put("message", message);
                    }
                    return new ModelAndView(m, "messages.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/create-user",
                (request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    if (!pwMap.containsKey(username)) {
                        user = new User(username, password);
                        //userList.add(user);
                        pwMap.put("username", user);
                        response.redirect("/");
                        return "";
                    }
                    else {
                        if (pwMap.get(username).getPassword().equals(password)) {
                            response.redirect("messages.html");
                            return "";
                        }
                        else {
                            Spark.halt("Incorrect password.");
                        }
                    }
                    return "";
                }
        );
        Spark.post(
                "/create-message",
                (request, response) -> {
                    String newMessage = request.queryParams("message");
                    Message message = new Message(newMessage);
                    messageArrayList.add(message);
                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    user = null;
                    response.redirect("/");
                    return "";
                }
        );
    }
}
