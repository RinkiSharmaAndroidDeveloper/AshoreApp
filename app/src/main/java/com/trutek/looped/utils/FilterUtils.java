package com.trutek.looped.utils;

import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;

/**
 * Created by msas on 10/19/2016.
 */
public class FilterUtils {

    private static FilterUtils _context;

    private PageQuery _query;
    private PageInput _discoverPeopleInput;
    private PageInput _discoverCommunityInput;

    private FilterUtils() {

        _query = new PageQuery();
        _discoverPeopleInput = new PageInput(_query);
       _discoverCommunityInput = new PageInput(_query);
    }

    public static FilterUtils getInstance() {
        if (_context == null) {
            _context = new FilterUtils();
        }
        return _context;
    }

    public PageQuery getQuery() {
        return _query;
    }

    public void resetQuery() {
        _query.reset();
    }



}
