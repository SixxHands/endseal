package com.retro.endseal.config;

import com.retro.endseal.EndSealException;

import java.util.ArrayList;
import java.util.List;

// NeoForge throws IllegalStateException if config values are accessed before the TOML file finishes loading.
public final class NeoForgeSealSettings implements ISealSettings
{
    @Override
    public boolean isSealed() throws EndSealException
    {
        try
        {
            return EndSealConfig.Sealed.get();
        }
        catch (IllegalStateException ex)
        {
            throw new EndSealException("EndSeal config is not loaded yet.", ex);
        }
    }

    @Override
    public boolean allowsPartialFrame() throws EndSealException
    {
        try
        {
            return EndSealConfig.AllowPartialFrame.get();
        }
        catch (IllegalStateException ex)
        {
            throw new EndSealException("EndSeal config is not loaded yet.", ex);
        }
    }

    @Override
    public List<String> refusalMessages() throws EndSealException
    {
        try
        {
            List<? extends String> configured = EndSealConfig.Messages.get();
            if (configured == null || configured.isEmpty())
            {
                return List.of();
            }
            List<String> lines = new ArrayList<>(configured.size());
            for (String line : configured)
            {
                if (line != null && !line.isBlank())
                {
                    lines.add(line);
                }
            }
            return List.copyOf(lines);
        }
        catch (IllegalStateException ex)
        {
            throw new EndSealException("EndSeal config is not loaded yet.", ex);
        }
    }

    @Override
    public void setSealed(boolean sealed) throws EndSealException
    {
        try
        {
            EndSealConfig.Sealed.set(sealed);
            EndSealConfig.Sealed.save();
        }
        catch (IllegalStateException ex)
        {
            throw new EndSealException("Cannot update EndSeal config before it finishes loading.", ex);
        }
    }
}
