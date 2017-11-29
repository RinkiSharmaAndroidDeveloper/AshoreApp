package com.trutek.looped.msas.common.commands;

import android.os.Bundle;

public interface Command {

    void execute(Bundle bundle) throws Exception;
}