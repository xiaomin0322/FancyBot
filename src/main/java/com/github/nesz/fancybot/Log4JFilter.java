package com.github.nesz.fancybot;

import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "Log4JFilter", category = Core.CATEGORY_NAME, elementType = Filter.ELEMENT_TYPE)
public class Log4JFilter extends AbstractFilter
{

    @PluginFactory
    public static Log4JFilter createFilter()
    {
        return new Log4JFilter();
    }

    @Override
    public Result filter(final LogEvent event)
    {
        if (event.getThrown() instanceof ErrorResponseException)
        {
            final ErrorResponseException response = (ErrorResponseException) event.getThrown();

            if (response.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE)
            {
                return Result.DENY;
            }
        }

        return Result.ACCEPT;
    }
}
