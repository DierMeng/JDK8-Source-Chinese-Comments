package com.sun.corba.se.impl.orbutil.concurrent;

public interface Sync {

  public void acquire() throws InterruptedException;

  public boolean attempt(long msecs) throws InterruptedException;

  public void release();

  public static final long ONE_SECOND = 1000;

  public static final long ONE_MINUTE = 60 * ONE_SECOND;

  public static final long ONE_HOUR = 60 * ONE_MINUTE;

  public static final long ONE_DAY = 24 * ONE_HOUR;

  public static final long ONE_WEEK = 7 * ONE_DAY;

  public static final long ONE_YEAR = (long)(365.2425 * ONE_DAY);

  public static final long ONE_CENTURY = 100 * ONE_YEAR;


}
