/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.swt.internal.events.EventLCAUtil.containsEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.scripting.ClientListener;
import org.junit.Before;
import org.junit.Test;


public class EventTable_Test {

  private static final int EVENT_1 = 1;
  private static final int EVENT_2 = 2;
  private EventTable eventTable;

  @Before
  public void setUp() {
    eventTable = new EventTable();
  }

  @Test
  public void testHook() {
    Listener listener = mock( Listener.class );

    eventTable.hook( EVENT_1, listener );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testUnhook() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( EVENT_1, listener );

    assertEquals( 0, eventTable.size() );
  }

  @Test
  public void testUnhookUnknownEventType() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( 23, listener );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testUnhookUnknownListener() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );

    eventTable.unhook( EVENT_1, mock( Listener.class ) );

    assertEquals( 1, eventTable.size() );
  }

  @Test
  public void testHooksUnknownEventType() {
    boolean hooks = eventTable.hooks( 23 );

    assertFalse( hooks );
  }

  @Test
  public void testHooksKnownEventType() {
    eventTable.hook( EVENT_1, mock( Listener.class ) );

    boolean hooks = eventTable.hooks( EVENT_1 );

    assertTrue( hooks );
  }

  @Test
  public void testGetEventList_initiallyEmpty() {
    assertEquals( 0, eventTable.getEventList() );
  }

  @Test
  public void testGetEventList_containsHookedEvents() {
    eventTable.hook( EVENT_1, mock( Listener.class ) );

    long eventList = eventTable.getEventList();

    assertTrue( containsEvent( eventList, EVENT_1 ) );
  }

  @Test
  public void testGetEventList_containsMultipleHookedEvents() {
    eventTable.hook( EVENT_1, mock( Listener.class ) );
    eventTable.hook( EVENT_2, mock( Listener.class ) );

    long eventList = eventTable.getEventList();

    assertTrue( containsEvent( eventList, EVENT_1 ) );
    assertTrue( containsEvent( eventList, EVENT_2 ) );
  }

  @Test
  public void testGetEventList_doesNotContainUnhookedEvents() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );
    eventTable.hook( EVENT_2, listener );
    eventTable.unhook( EVENT_1, listener );

    long eventList = eventTable.getEventList();

    assertFalse( containsEvent( eventList, EVENT_1 ) );
    assertTrue( containsEvent( eventList, EVENT_2 ) );
  }

  @Test
  public void testGetEventList_containsPartlyUnhookedEvents() {
    Listener listener = mock( Listener.class );
    eventTable.hook( EVENT_1, listener );
    eventTable.hook( EVENT_1, mock( Listener.class ) );
    eventTable.unhook( EVENT_1, listener );

    long eventList = eventTable.getEventList();

    assertTrue( containsEvent( eventList, EVENT_1 ) );
  }

  @Test
  public void testGetEventList_doesNotContainClientListeners() {
    Listener listener = mock( ClientListener.class );
    eventTable.hook( EVENT_1, listener );

    long eventList = eventTable.getEventList();

    assertFalse( containsEvent( eventList, EVENT_1 ) );
  }

}
