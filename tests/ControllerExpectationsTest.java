package dev.gbppimentel.zzz;
public final class ControllerExpectationsTest {
 static int checks;static void check(boolean v){if(!v)throw new AssertionError("Controller acknowledgment "+checks);checks++;}
 public static void main(String[] args){ControllerExpectations e=new ControllerExpectations();e.expect(1,304,1,100);check(!e.consume(1,305,1,101));check(e.consume(1,304,1,102));check(!e.consume(1,304,1,103));e.expect(1,304,0,200);check(!e.consume(1,304,1,201));check(e.consume(1,304,0,202));e.expect(3,17,1,300);check(!e.consume(3,17,1,1301));e.expect(3,9,32767,2000);check(!e.consume(3,10,32767,2001));check(e.consume(3,9,32767,2002));System.out.println(checks+" controller acknowledgment checks passed");}
}
