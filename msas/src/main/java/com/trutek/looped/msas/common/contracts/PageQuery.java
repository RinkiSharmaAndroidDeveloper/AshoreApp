package com.trutek.looped.msas.common.contracts;

import android.net.Uri;

import com.trutek.looped.msas.common.helpers.DateHelper;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class PageQuery {
    Boolean isEduOrNot = false;
    private Hashtable<String, Object> _data = new Hashtable<>();

    public PageQuery() {
    }

    public PageQuery(String key, String value) {
        add(key, value);
    }

    public PageQuery(String key, Long value) {
        add(key, value);
    }

    public PageQuery(String key, Boolean value) {
        add(key, value);
    }

    public PageQuery(String key, Date value) {
        add(key, value);
    }

    public PageQuery(String key, List<String> value) {
        add(key, value);
    }

    public PageQuery reset() {
        _data = new Hashtable<>();
        return this;
    }

    public PageQuery remove(String key) {
        if (_data.containsKey(key)) {
            _data.remove(key);
        }
        return this;
    }


    public PageQuery add(String key, String value) {
        if (value != null) {
            _data.put(key, value);
        }
        return this;
    }

    public PageQuery add(String key, Date value) {
        if (value != null) {
            _data.put(key, value);
        }
        return this;
    }

    public PageQuery add(String key, Integer value) {
        if (value != null) {
            _data.put(key, value);
        }
        return this;
    }

    public PageQuery add(String key, Long value) {
        if (value != null) {
            _data.put(key, value);
        }
        return this;
    }

    public PageQuery add(String key, Boolean value) {
        if (value != null) {
            _data.put(key, value);
        }
        return this;
    }

    public PageQuery add(String key, List<String> value) {
        if (value.size() > 0) {
            for (String obj : value) {
                _data.put(key, obj);
            }
        }
        return this;
    }

    public boolean contains(String key) {
        return _data.containsKey(key);
    }

    public String getString(String key) {
        return (String) _data.get(key);
    }

    public Boolean getBoolean(String key) {
        if (!_data.containsKey(key)) {
            return false;
        }
        return (Boolean) _data.get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) _data.get(key);
    }

    public Long getLong(String key) {
        return (Long) _data.get(key);
    }

    public Date getDate(String key) {
        return (Date) _data.get(key);
    }

    public Set<String> keys() {
        return _data.keySet();
    }

    public Hashtable<String, String> getParams() {
        Hashtable<String, String> params = new Hashtable<>();

        for (String key : _data.keySet()) {
            Object value = _data.get(key);

            if (value.getClass().equals(Integer.class)) {
                params.put(key, String.valueOf((Integer) value));
            } else if (value.getClass().equals(Date.class)) {
                params.put(key, DateHelper.stringify((Date) value, DateHelper.StringifyAs.Utc));
            } else if (value.getClass().equals(Long.class)) {
                params.put(key, String.valueOf((Long) value));
            } else if (value.getClass().equals(Boolean.class)) {
                params.put(key, String.valueOf((Boolean) value));
            } else if (value.getClass().equals(String.class)) {
                params.put(key, (String) value);
            } else if (value.getClass().equals(List.class)) {
                params.put(key, (String) value);
            }
        }
        return params;
    }

    public String toString(Boolean IsEdu,Hashtable<String, String> params ) {
        isEduOrNot = IsEdu;
        Uri.Builder uriBuilder = new Uri.Builder();
        if (isEduOrNot) {
            if (params != null) {
                int count=0;
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    if (!value.equals("")) {
                        switch (key) {
                            case "page":
                                uriBuilder.appendQueryParameter(key, value);
                                break;
                            case "noPaging":
                                uriBuilder.appendQueryParameter(key, value);
                                break;
                            default:
                                uriBuilder.appendQueryParameter("f" + "[" + count + "]" + "[f]", key);
                                uriBuilder.appendQueryParameter("f" + "[" + count + "]" + "[v]", value);
                                uriBuilder.appendQueryParameter("f" + "[" + count + "]" + "[o]", "eq");
                                count++;
                                break;
                        }
                    }
                }
            }
        } else {
            if (params == null || params.size() == 0) {
                return "_";
            }
            for (String key : params.keySet()) {
                String value = params.get(key);
                if (!value.equals(""))
                    uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().getEncodedQuery();
    }
}
