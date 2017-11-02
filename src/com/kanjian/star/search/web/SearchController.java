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

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.kanjian.star.search.services.IndexBuilder;
import com.kanjian.star.search.services.TrackSearcher;
import com.kanjian.star.search.services.AlbumSearcher;


@Controller
public class SearchController {

    private static final Logger logger = Logger.getLogger(SearchController.class);

    @Autowired
    private IndexBuilder indexBuilder;

    @RequestMapping(value="/health_check", method = RequestMethod.GET)
    public String healthCheck(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("ok");

        return null;
    }

    @RequestMapping(value="/build_index", method = RequestMethod.GET)
    public String buildIndex(HttpServletResponse response) throws IOException {
        indexBuilder.buildIndex();
        response.getWriter().write("ok");

        return null;
    }

    @RequestMapping(value="/reload_index", method = RequestMethod.GET)
    public String reloadIndex(HttpServletResponse response) throws IOException {
        TrackSearcher.reloadIndex();
        AlbumSearcher.reloadIndex();
        response.getWriter().write("ok");

        return null;
    }

    @RequestMapping(value="/search_track", method = RequestMethod.GET)
    public String searchTrack(
			@RequestParam(value="query", required=false) String query,
			@RequestParam(value="start", required=false) String start,
			@RequestParam(value="rows", required=false) String rows,
            HttpServletResponse response) throws IOException, ParseException {
        int qs = 0;
        int qr = 20;
        if (start != null) qs = Integer.parseInt(start);
        if (rows != null) qr = Integer.parseInt(rows);
        List<Integer> results = TrackSearcher.searchTrack(query, qs, qr);
        JsonObject ret = new JsonObject();
        ret.addProperty("error_status", 0);
        JsonElement ja = new Gson().toJsonTree(results, new TypeToken<List<Integer>>() {}.getType());
        ret.add("ret", ja);
        response.getWriter().write(ret.toString());

        return null;
    }

    @RequestMapping(value="/search_album", method = RequestMethod.GET)
    public String searchAlbum(
			@RequestParam(value="query", required=false) String query,
			@RequestParam(value="start", required=false) String start,
			@RequestParam(value="rows", required=false) String rows,
            HttpServletResponse response) throws IOException, ParseException {
        int qs = 0;
        int qr = 20;
        if (start != null) qs = Integer.parseInt(start);
        if (rows != null) qr = Integer.parseInt(rows);
        List<Integer> results = AlbumSearcher.searchAlbum(query, qs, qr);
        JsonObject ret = new JsonObject();
        ret.addProperty("error_status", 0);
        JsonElement ja = new Gson().toJsonTree(results, new TypeToken<List<Integer>>() {}.getType());
        ret.add("ret", ja);
        response.getWriter().write(ret.toString());

        return null;
    }
}
