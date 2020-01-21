/*
 * Copyright 2019 mk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tt.badu3;

/**
 * @author mk
 * See: https://stackoverflow.com/questions/75218/how-can-i-detect-when-an-exceptions-been-thrown-globally-in-java/75439
 */
class ExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private final ExceptionCallback callback;

    private ExceptionHandler(ExceptionCallback ec)
    {
        callback = ec;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        e.printStackTrace();
        handle(e);
    }

    private void handle(Throwable throwable)
    {
        try {
            callback.call(throwable);
        }
        catch (Throwable t) {
            // don't let the exception get thrown out, will cause infinite looping!
        }
    }

    public static void registerExceptionHandler(ExceptionCallback ec)
    {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(ec));
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
    }

    public interface ExceptionCallback
    {
        void call(Throwable t);
    }
}
