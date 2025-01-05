package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.domain.Memo;

import java.util.Map;

@SuppressWarnings("all")
public interface MemoService {

    boolean saveMemo(Memo memo);

    Memo getMemo(String date);

    Map<String, Object> getMemoList(String date);
}
