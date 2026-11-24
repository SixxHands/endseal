package com.retro.endseal;

public final class EndSealException extends Exception
{
    private static final long serialVersionUID = 1L;

    public EndSealException(String message)
    {
        super(message);
    }

    public EndSealException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
