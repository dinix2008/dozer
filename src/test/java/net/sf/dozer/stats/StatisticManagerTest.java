/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import net.sf.dozer.AbstractDozerTest;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class StatisticManagerTest extends AbstractDozerTest {
  private StatisticsManagerImpl statMgr;

  @Before
  public void setUp() throws Exception {
    statMgr = new StatisticsManagerImpl();
    statMgr.setStatisticsEnabled(true);
  }

  @Test
  public void testAddAndGetStatistic() {
    StatisticType type = StatisticType.FIELD_MAPPING_FAILURE_COUNT;
    Statistic stat = new Statistic(type);
    statMgr.addStatistic(stat);

    assertEquals("invalid stat size", 1, statMgr.getStatistics().size());
    assertEquals("invalid stat found", stat, statMgr.getStatistic(type));
    assertTrue("invalid stat ref found", stat == statMgr.getStatistic(type));
  }

  @Test
  public void testSetStatisticsEnabled() {
    boolean value = true;
    if (statMgr.isStatisticsEnabled()) {
      value = false;
    }
    statMgr.setStatisticsEnabled(value);
    assertEquals("invalid stats enabled value", value, statMgr.isStatisticsEnabled());
  }

  @Test
  public void testClearAll() {
    statMgr.addStatistic(new Statistic(StatisticType.MAPPING_FAILURE_TYPE_COUNT));
    statMgr.addStatistic(new Statistic(StatisticType.CACHE_HIT_COUNT));
    assertEquals("invalid initial stat size", 2, statMgr.getStatistics().size());
    statMgr.clearAll();
    assertEquals("invalid stat size", 0, statMgr.getStatistics().size());
  }

  @Test
  public void testGetStatisticTypes() {
    StatisticType type = StatisticType.CUSTOM_CONVERTER_TIME;
    StatisticType type2 = StatisticType.CACHE_HIT_COUNT;
    statMgr.addStatistic(new Statistic(type));
    statMgr.addStatistic(new Statistic(type2));

    Set expected = new HashSet();
    expected.add(type);
    expected.add(type2);

    assertEquals("invalid stat types found", expected, statMgr.getStatisticTypes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementMissingParams() {
    statMgr.increment(null, "test", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIncrementMissingParams2() {
    statMgr.increment(StatisticType.CACHE_HIT_COUNT, null, 1);
  }

  @Test
  public void testGetStatisticValue() {
    StatisticType type = StatisticType.MAPPER_INSTANCES_COUNT;
    Statistic stat = new Statistic(type);
    String entryKey = getRandomString();
    stat.addEntry(new StatisticEntry(entryKey));

    statMgr.addStatistic(stat);
    statMgr.increment(type, entryKey, 100);

    assertEquals("invalid entry value", 100, statMgr.getStatisticValue(type));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStatisticValueException() {
    StatisticType type = StatisticType.MAPPING_FAILURE_COUNT;
    Statistic stat = new Statistic(type);

    stat.addEntry(new StatisticEntry(getRandomString()));
    stat.addEntry(new StatisticEntry(getRandomString()));
    statMgr.addStatistic(stat);
    statMgr.getStatisticValue(type);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddDuplicateStatistic() {
    StatisticType type = StatisticType.CUSTOM_CONVERTER_SUCCESS_COUNT;
    statMgr.addStatistic(new Statistic(type));
    statMgr.addStatistic(new Statistic(type));
  }

  @Test
  public void testIncrementUnknownTypeAndKey() {
    StatisticType type = StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT;
    String entryKey = getRandomString();
    long incrementValue = 130;
    statMgr.increment(type, entryKey, incrementValue);

    assertEquals("invalid stat entry value", incrementValue, statMgr.getStatisticValue(type));
  }

}
