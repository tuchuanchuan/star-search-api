package com.kanjian.star.search.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


@Repository
public class StarDAO {

	private static final Logger logger = Logger.getLogger(StarDAO.class);

    @Autowired
    protected SqlSessionTemplate sqlSession;

    @Value("${hop_each_build}")
    private String hopEachBuild;

    public List<Integer> getTrackList() {
        List<Integer> result = new ArrayList<Integer>();
        Map<String, Object> paras = new HashMap<String, Object>();
        paras.put("start", 0);
        paras.put("end", 500);
        List<HashMap<String, Object>> list = sqlSession.selectList("getAllTrack", paras);
        for (HashMap<String, Object> map: list) {
            result.add((Integer)map.get("id"));
        }
        return result;
    }
}
