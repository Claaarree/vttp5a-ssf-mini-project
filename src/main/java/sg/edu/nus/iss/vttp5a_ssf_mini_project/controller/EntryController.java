package sg.edu.nus.iss.vttp5a_ssf_mini_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.vttp5a_ssf_mini_project.model.Entry;

@Controller
@RequestMapping("/entries")
public class EntryController {

    @GetMapping("/new")
    public ModelAndView newEntryForm() {
        Entry entry = new Entry();
        String url = UriComponentsBuilder.fromPath("/entries/new")
                // either one works: path segment adds the / for you
                .pathSegment("{entryId}")
                // .path("/{entryId}")
                .build(entry.getEntryId())
                .toString();
               
        System.out.println(url);
        ModelAndView mav = new ModelAndView("redirect:" + url);

        return mav;
    }

    @GetMapping("/new/{entryId}")
    public ModelAndView entryWithId(@PathVariable String entryId) {
        ModelAndView mav = new ModelAndView("addEntry");
        Entry entry = new Entry();
        entry.setEntryId(entryId);
        mav.addObject("entry", entry);
        return mav;
    }
    
}
