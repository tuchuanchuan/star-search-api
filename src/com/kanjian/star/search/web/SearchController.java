package com.kanjian.star.search.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.kanjian.star.search.dao.StarDAO;

@Controller
@RequestMapping(value="/health_check")
public class SearchController {

    private static final Logger logger = Logger.getLogger(SearchController.class);

    @Autowired
    private StarDAO dao;

    @RequestMapping(method = RequestMethod.GET)
    public String healthCheck(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("ok");
        logger.info(dao.getTrackList().size());
        return null;
    }
}
