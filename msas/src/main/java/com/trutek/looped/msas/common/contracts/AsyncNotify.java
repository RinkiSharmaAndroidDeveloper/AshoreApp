package com.trutek.looped.msas.common.contracts;

public interface AsyncNotify {
    void success();

    void error(String error);
}
