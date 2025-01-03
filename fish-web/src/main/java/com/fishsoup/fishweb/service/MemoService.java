package com.fishsoup.fishweb.service;

import com.fishsoup.fishweb.domain.Memo;

@SuppressWarnings("all")
public interface MemoService {

    boolean saveMemo(Memo memo);

    Memo getMemo(String date);
}
