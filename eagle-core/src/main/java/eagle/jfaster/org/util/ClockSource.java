/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.util;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Created by fangyanpeng1 on 2017/8/2.
 */
public interface ClockSource {

    ClockSource INSTANCE = Factory.create();

    ClockSource MILLINSTANCE = new MillisecondClockSource();

    long currentTime();

    long toMillis(long time);

    long toNanos(long time);

    long elapsedMillis(long startTime);

    long elapsedMillis(long startTime, long endTime);

    long elapsedNanos(long startTime);

    long elapsedNanos(long startTime, long endTime);

    long plusMillis(long time, long millis);

    TimeUnit getSourceTimeUnit();

    String elapsedDisplayString(long startTime, long endTime);

    TimeUnit[] TIMEUNITS_DESCENDING = {DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS};

    String[] TIMEUNIT_DISPLAY_VALUES = {"ns", "μs", "ms", "s", "m", "h", "d"};

    class Factory
    {
        private static ClockSource create()
        {
            String os = System.getProperty("os.name");
            if ("Mac OS X".equals(os)) {
                return new MillisecondClockSource();
            }

            return new NanosecondClockSource();
        }
    }

    final class MillisecondClockSource extends NanosecondClockSource
    {

        @Override
        public long currentTime()
        {
            return System.currentTimeMillis();
        }


        @Override
        public long elapsedMillis(final long startTime)
        {
            return System.currentTimeMillis() - startTime;
        }

        @Override
        public long elapsedMillis(final long startTime, final long endTime)
        {
            return endTime - startTime;
        }

        @Override
        public long elapsedNanos(final long startTime)
        {
            return MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
        }

        @Override
        public long elapsedNanos(final long startTime, final long endTime)
        {
            return MILLISECONDS.toNanos(endTime - startTime);
        }

        @Override
        public long toMillis(final long time)
        {
            return time;
        }

        @Override
        public long toNanos(final long time)
        {
            return MILLISECONDS.toNanos(time);
        }

        @Override
        public long plusMillis(final long time, final long millis)
        {
            return time + millis;
        }

        @Override
        public TimeUnit getSourceTimeUnit()
        {
            return MILLISECONDS;
        }
    }

    class NanosecondClockSource implements ClockSource
    {
        @Override
        public long currentTime()
        {
            return System.nanoTime();
        }

        @Override
        public long toMillis(final long time)
        {
            return NANOSECONDS.toMillis(time);
        }

        @Override
        public long toNanos(final long time)
        {
            return time;
        }

        @Override
        public long elapsedMillis(final long startTime)
        {
            return NANOSECONDS.toMillis(System.nanoTime() - startTime);
        }

        @Override
        public long elapsedMillis(final long startTime, final long endTime)
        {
            return NANOSECONDS.toMillis(endTime - startTime);
        }

        @Override
        public long elapsedNanos(final long startTime)
        {
            return System.nanoTime() - startTime;
        }

        @Override
        public long elapsedNanos(final long startTime, final long endTime)
        {
            return endTime - startTime;
        }

        @Override
        public long plusMillis(final long time, final long millis)
        {
            return time + MILLISECONDS.toNanos(millis);
        }

        @Override
        public TimeUnit getSourceTimeUnit()
        {
            return NANOSECONDS;
        }

        @Override
        public String elapsedDisplayString(long startTime, long endTime)
        {
            long elapsedNanos = elapsedNanos(startTime, endTime);

            StringBuilder sb = new StringBuilder(elapsedNanos < 0 ? "-" : "");
            elapsedNanos = Math.abs(elapsedNanos);

            for (TimeUnit unit : TIMEUNITS_DESCENDING) {
                long converted = unit.convert(elapsedNanos, NANOSECONDS);
                if (converted > 0) {
                    sb.append(converted).append(TIMEUNIT_DISPLAY_VALUES[unit.ordinal()]);
                    elapsedNanos -= NANOSECONDS.convert(converted, unit);
                }
            }

            return sb.toString();
        }
    }
}
