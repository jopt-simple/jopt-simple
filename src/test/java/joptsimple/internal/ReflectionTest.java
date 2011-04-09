/*
 The MIT License

 Copyright (c) 2004-2011 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package joptsimple.internal;

import java.lang.reflect.Method;

import static joptsimple.internal.Reflection.*;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.*;

/**
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public class ReflectionTest {
    @Test
    public void invokingConstructorQuietlyShouldWrapInstantiationException() throws Exception {
        try {
            instantiate( AbstractProblematic.class.getDeclaredConstructor() );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = InstantiationException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    @Test
    public void invokingConstructorQuietlyShouldWrapIllegalAccessException() throws Exception {
        try {
            instantiate( Problematic.class.getDeclaredConstructor() );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = IllegalAccessException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    @Test
    public void invokingConstructorQuietlyShouldWrapCauseOfInvocationTargetException() throws Exception {
        try {
            instantiate( Problematic.class.getDeclaredConstructor( String.class ), "arg" );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = IllegalStateException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    @Test
    public void invokingConstructorQuietlyShouldWrapIllegalArgumentException() throws Exception {
        try {
            instantiate( Problematic.class.getDeclaredConstructor( String.class ) );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = IllegalArgumentException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    @Test
    public void invokingStaticMethodQuietlyShouldWrapIllegalAccessException() throws Exception {
        Method method = Problematic.class.getDeclaredMethod( "boo" );

        try {
            invoke( method );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = IllegalAccessException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    @Test
    public void invokingStaticMethodQuietlyShouldWrapIllegalArgumentException() throws Exception {
        Method method = Problematic.class.getDeclaredMethod( "mute" );

        try {
            invoke( method, new Object() );
            fail();
        }
        catch ( ReflectionException expected ) {
            String causeName = IllegalArgumentException.class.getName();
            assertThat( expected.getMessage(), containsString( causeName ) );
        }
    }

    private abstract static class AbstractProblematic {
        protected AbstractProblematic() {
            // no-op
        }
    }
}
