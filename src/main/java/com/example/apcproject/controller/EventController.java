package com.example.apcproject.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.apcproject.model.Event;
import com.example.apcproject.service.EventService;

@Controller
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;
    
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping
    public String eventsPage(Model model, Authentication auth) {
        List<Event> publicEvents = eventService.getPublicEvents();
        model.addAttribute("events", publicEvents);
        
        if (auth != null && auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            model.addAttribute("userEmail", email);
            model.addAttribute("userName", name);
            model.addAttribute("isOAuth2User", true);
        }
        
        return "events";
    }
    
    @GetMapping("/create")
    public String createEventForm(Model model, Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof OAuth2User)) {
            return "redirect:/login";
        }
        
        OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        Event event = new Event();
        event.setOrganizerEmail(email);
        event.setOrganizerName(name);
        model.addAttribute("event", event);
        model.addAttribute("userEmail", email);
        model.addAttribute("userName", name);
        
        return "create-event";
    }
    
    @PostMapping("/create")
    public String createEvent(@ModelAttribute Event event, Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof OAuth2User)) {
            return "redirect:/login";
        }
        
        OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        event.setOrganizerEmail(email);
        event.setOrganizerName(name);
        eventService.createEvent(event);
        
        return "redirect:/events";
    }
    
    @GetMapping("/my-events")
    public String myEvents(Model model, Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof OAuth2User)) {
            return "redirect:/login";
        }
        
        OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        List<Event> myEvents = eventService.getEventsByOrganizer(email);
        model.addAttribute("events", myEvents);
        model.addAttribute("userEmail", email);
        model.addAttribute("userName", name);
        
        return "my-events";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable String id, Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof OAuth2User)) {
            return "redirect:/login";
        }
        
        OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
        String email = oauth2User.getAttribute("email");
        
        Event event = eventService.getEventById(id);
        if (event != null && event.getOrganizerEmail().equals(email)) {
            eventService.deleteEvent(id);
        }
        
        return "redirect:/events/my-events";
    }
}
