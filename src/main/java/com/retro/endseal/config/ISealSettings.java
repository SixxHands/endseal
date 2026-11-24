package com.retro.endseal.config;

import com.retro.endseal.EndSealException;

import java.util.List;

public interface ISealSettings
{
    boolean isSealed() throws EndSealException;

    boolean allowsPartialFrame() throws EndSealException;

    List<String> refusalMessages() throws EndSealException;

    void setSealed(boolean sealed) throws EndSealException;
}
