type State "on"|"off";

function finiteAssignmentStateType() returns State {
    State p = "on";
    State comparator = "on";
    if (p == comparator) {
       p = "off";
    }
    return p;
}

type NumberSet 1|2|3|4|5;

function finiteAssignmentNumberSetType() returns NumberSet {
    NumberSet n = 1;
    NumberSet comparator = 1;
    if (n == comparator) {
       n = 5;
    }
    return n;
}

type StringOrInt int|string;

function finiteAssignmentStringOrIntSetType() returns StringOrInt {
    StringOrInt si = 1;
    StringOrInt comparator = 1;
    if (si == comparator) {
       si = "This is a string";
    }
    return si;
}

function finiteAssignmentStringOrIntSetTypeCaseTwo() returns StringOrInt {
    StringOrInt si = "This is a string";
    StringOrInt comparator = "This is a string";
    if (si == comparator) {
       si = 111;
    }
    return si;
}

type Int int;

function finiteAssignmentIntSetType() returns Int {
    Int si = 1;
    Int comparator = 1;
    if (si == comparator) {
       si = 222;
    }
    return si;
}

function finiteAssignmentIntArrayType() returns Int {
    Int[] si = [];
    si[0] = 10001;
    si[1] = 2345;
    Int comparator = 2345;
    if (si[1] == comparator){
        si[1] = 9989;
    }
    return si[1];
}

function finiteAssignmentStateSameTypeComparison() returns int {
    State a = "off";
    State b = "on";
    if (a == b){
       return 1;
    }
    return 2;
}

function finiteAssignmentStateSameTypeComparisonCaseTwo() returns State {
    State a = "off";
    State b = "on";
    if (a != b){
       a = b;
       return a;
    }
    return b;
}

type POrInt Person|int;

type Person record {
   string name;
};

function finiteAssignmentRefValueType() returns POrInt {
    Person p = {name:"abc"};
    POrInt pi = p;
    return pi;
}

function finiteAssignmentRefValueTypeCaseTwo() returns POrInt {
    Person p = {name:"abc"};
    POrInt pi = 4;
    return pi;
}

type PreparedResult "ss"|"sss"|"qqq";

function testFiniteTypeWithTypeCheck() returns PreparedResult {
    var result = foo();
    if (result is PreparedResult) {
        return result;
    } else {
        return "qqq";
    }
}

function foo() returns PreparedResult | error | () {
       PreparedResult x = "ss";
       return x;
}


function testFiniteTypesWithDefaultValues() returns State {
   return assignFiniteValueAsDefaultParam();
}

function assignFiniteValueAsDefaultParam(State cd = "on") returns State {
   Channel c = new(b = cd);
   return c.b ?: "off";
}

class Channel {

    public State? b;

    function init (State b = "off", boolean a = true){
        self.b = b;
        State o =  "on";
        if(self.b == o) {
           int i = 4;
        }
    }
}

type CombinedState "on"|"off"|int;

function testFiniteTypesWithUnion() returns CombinedState {
   CombinedState abc = 1;
   return abc;
}

function testFiniteTypesWithUnionCaseOne() returns CombinedState {
   CombinedState abc = "off";
   if( abc == "off"){
       return 100;
   }
   return 0;
}

function testFiniteTypesWithUnionCaseTwo() returns CombinedState {
   CombinedState abc = "off";
   if( abc == "off"){
       return "on";
   }
   return "off";
}

function testFiniteTypesWithUnionCaseThree() returns int {
   CombinedState abc = "off";
   if( abc == "off"){
       return 1001;
   }
   return 1002;
}

function testFiniteTypesWithTuple() returns State {
   State onState = "on";
   [State, int ] b = [onState, 20];
   var [i, j] = b;
   return i;
}

type TypeAliasOne Person;

type TypeAliasTwo TypeAliasOne;

type TypeAliasThree TypeAliasTwo;

function testTypeAliasing() returns string {
    TypeAliasThree p = {name:"Anonymous name"};
    return p.name;
}

type MyType int|string;

function testTypeAliasingCaseOne() returns [MyType,MyType] {
     MyType a = 100;
     MyType b = "hundred";
     return [a,b];
}

public type ParamTest string|int;

function testTypeDefinitionWithVarArgs() returns [ParamTest, ParamTest] {
    string s1 = "Anne";
    ParamTest p1 = testVarArgs("John");
    ParamTest p2 = testVarArgs(s1);
    return [p1, p2];
}

function testVarArgs(ParamTest... p1) returns ParamTest {
    return p1[0];
}

type ArrayCustom int[];

function testTypeDefinitionWithArray() returns [int, int] {
    ArrayCustom val = [34, 23];
    return [val.length() , val[1]];
}

type FuncType function (string) returns int;

function testTypeDefWithFunctions() returns int {
    FuncType fn = function (string s) returns int {
        return s.length();
    };
    return fn("Hello");
}

type FuncType2 (function (string) returns int)|string;

function testTypeDefWithFunctions2() returns int {
    FuncType2 fn = function (string s) returns int {
        return s.length();
    };

    if (fn is function (string) returns int) {
        return fn("Hello");
    }

    return -1;
}

const int ICON = 5;
const string SCON = "s";

type FiniteType ICON|SCON;

function testFiniteTypeWithConstants() returns [FiniteType, FiniteType] {
    FiniteType f = 5;
    FiniteType s = "s";

    return [f,s];
}

const byte BCONST = 5;
const int ICONST = 5;
const float FCONST = 5;
const decimal DCONST = 5;

type Number DCONST|FCONST|ICONST|BCONST;

function testFiniteTypeWithNumericConstants() returns [Number, Number] {
    Number n1 = 5;
    Number n2 = 5.0;
    return [n1, n2];
}

type ByteType BCONST;

function testAssigningIntLiteralToByteFiniteType() returns (ByteType) {
    ByteType b = 5;
    return b;
}

type FloatType FCONST;

function testAssigningIntLiteralToFloatFiniteType() returns (FloatType) {
    FloatType f = 5;
    return f;
}

type DecimalType DCONST;

function testAssigningIntLiteralToDecimalFiniteType() returns (DecimalType) {
    DecimalType d = 5;
    return d;
}

function testAssigningFloatLiteralToDecimalFiniteType() returns (DecimalType) {
    DecimalType d = 5.0;
    return d;
}

function testDifferentPrecisionFloatAssignment() returns (FloatType) {
    // Though, the below value is mathematically different when comparing to 5.0, the double value(representation)
    // is same. Hence, it is assignable to 5.0 float value.
    FloatType f = 5.00000000000000000001;
    return f;
}

const float FLOAT = 5.0000000000000;

function testDifferentPrecisionFloatConstantAssignment() returns (FloatType) {
    FloatType f = FLOAT;
    return f;
}

function testDifferentPrecisionDecimalAssignment() returns (DecimalType) {
    DecimalType d = 5.0000000000000;
    return d;
}

const decimal DECIMAL = 5.0000000000000;


function testDifferentPrecisionDecimalConstantAssignment() returns (DecimalType) {
    DecimalType d = DECIMAL;
    return d;
}

function testStringOnlyFiniteTypeAssignmentToTypeWithString() returns boolean {
    State a = "on";
    string b = a;
    boolean assignmentSuccessful = a == b;

    string|boolean c = a;
    return assignmentSuccessful && a == c;
}

function testIntOnlyFiniteTypeAssignmentToTypeWithInt() returns boolean {
    NumberSet a = 2;
    int b = a;
    boolean assignmentSuccessful = a == b;

    string|int c = a;
    return assignmentSuccessful && a == c;
}

type FloatValue 1.0|2.0;

function testFloatOnlyFiniteTypeAssignmentToTypeWithFloat() returns boolean {
    FloatValue a = 2.0;
    float b = a;
    boolean assignmentSuccessful = a == b;

    float|int c = a;
    return assignmentSuccessful && a == c;
}

type BooleanValue true;

function testBooleanOnlyFiniteTypeAssignmentToTypeWithBoolean() returns boolean {
    BooleanValue a = true;
    boolean b = a;
    boolean assignmentSuccessful = a == b;

    anydata c = a;
    return assignmentSuccessful && a == c;
}

const byte byte1 = 34;
const byte byte2 = 12;
const byte byte3 = 111;

type ByteValue byte1|byte2|byte3;

function testByteOnlyFiniteTypeAssignmentToTypeWithByte() returns boolean {
    ByteValue a = 12;
    byte b = a;
    boolean assignmentSuccessful = a == b;

    byte|Person c = a;
    return assignmentSuccessful && a == c;
}

function testFiniteTypeAssignmentToBroaderType() returns boolean {
    CombinedState a = "off";
    string|int b = a;
    boolean assignmentSuccessful = a == b;

    anydata c = a;
    assignmentSuccessful = assignmentSuccessful && a == c;

    StringOrInt d = a;
    assignmentSuccessful = assignmentSuccessful && a == d;

    b = d;
    return assignmentSuccessful && a == d;
}

const A = "a";

type AB A|"b";
type ABInt A|"b"|int;

function testFiniteTypeWithConstAssignmentToBroaderType() returns boolean {
    AB ab = A;
    string s = ab;
    boolean assignmentSuccessful = ab == s;

    ab = "b";
    s = ab;
    return assignmentSuccessful && ab == s;
}

function testFiniteTypeWithConstAndTypeAssignmentToBroaderType() returns boolean {
    ABInt ab = A;
    AB|int s = ab;
    boolean assignmentSuccessful = ab == s;

    ab = "b";
    string|int s2 = ab;
    assignmentSuccessful = assignmentSuccessful && ab == s2;

    ab = 12;
    s2 = ab;
    return assignmentSuccessful && ab == s2;
}

const FOO = "foo";

type W "foo"|"bar"|1|2.0|true|3;
type X boolean|FOO|"bar"|1|2.0|3;
type Y string|int|boolean|2.0;
type Z string|int|float|boolean;

function testFiniteTypesAsUnionsAsBroaderTypes_1() returns boolean {
    W a = "foo";
    X b = a;
    boolean assignmentSuccessful = a == b && b == FOO;

    a = true;
    b = a;
    assignmentSuccessful = assignmentSuccessful && a == b && b == true;

    a = 2.0;
    Y c = a;
    assignmentSuccessful = assignmentSuccessful && a == c && c == 2.0;

    a = 1;
    Z d = a;
    return assignmentSuccessful && a == d && a == 1;
}

function testFiniteTypesAsUnionsAsBroaderTypes_2() returns boolean {
    X a = true;
    Y b = a;
    boolean assignmentSuccessful = a == b && a == true;

    b = 2.0;
    Z c = b;
    return assignmentSuccessful && b == c && c == 2.0;
}

type t 1.0f|1.0d;
type t2 2.22f|3.33d;
function testFiniteTypesWithDiscriminatedMembers() returns [any, any, any, any, any] {
    t a = 1.0f;
    t b = 1.0d;
    t|t2 c = 2.22;
    t|t2 d = 2.22f;
    t|t2 e = 3.33d;
    return [a, b, c, d, e];
}

type PositiveInt +3|+5;

function testFiniteTypesWithPositiveIntegers() {
    PositiveInt n = +3;
    PositiveInt comparator = +3;
    if (n == comparator) {
       n = +5;
    }
    assertEquality(5,n);

}

type PositiveFloat +1.2|+1.5;

function testFiniteTypesWithPositiveFloats() {
    PositiveFloat n = +1.2;
    PositiveFloat comparator = +1.2;
    if (n == comparator) {
       n = +1.5;
    }
    assertEquality(1.5,n);
}

const CONST3 = ();

public function testNilFiniteType() {
    any v = CONST3;
    string a = "";
    string mah = "match";
    if (v is CONST3) {
        a = mah;
    }
    assertEquality(a, mah);
}

type FooString "foo";

type BarString "bar";

type FooInt 1;

type BarInt 2;

type FooBoolean true;

type BarBoolean false;

type RecString1 record {|
    FooString|BarString a;
    FooInt|BarInt b;
    FooBoolean|BarBoolean c;
|};

type RecString2 record {|
    string a;
    int b;
    boolean c;
|};

public function testRecordStringEquality() {
    RecString1 rec1 = {a: "foo", b: 1, c: true};

    RecString2 rec2 = rec1;

    string a = "";
    string mah = "match";
    if (<any>rec1 is RecString2) {
        a = mah;
    }
    assertEquality(a, mah);
}

public const '\- = "-";
public const d = "d";
public const s = "s";
public type FT '\-|s|d;

function testEscapedTypeName() returns FT {
   FT f = '\-;
   return f;
}

type Mat 1f|1d|2d;
type Mat2 1|1f|2d;
type Mat3 Mat|2f;

function testFiniteType() {
    Mat f1 = 1;
    Mat2 f2 = 1;
    Mat3 f3 = 1;
    "chiran"|5.0f f4 = 5;
    0x0.00p00 f5 = 0x0.00p00;
    assertEquality(f1 is 1f, true);
    assertEquality(f2 is 1, true);
    assertEquality(f3 is 1f, true);
    assertEquality(f4 is 5.0f, true);
    assertEquality(f5 is 0x0.00p00, true);
}

const ASSERTION_ERROR_REASON = "TypeAssertionError";

function assertEquality(any|error expected, any|error actual) {
    if (expected is anydata && actual is anydata && expected == actual) {
        return;
    }
    if (expected === actual) {
        return;
    }
    typedesc<any|error> tActual = typeof actual;

    string expectedValAsString = expected is error ? expected.toString() : expected.toString();
    panic error(ASSERTION_ERROR_REASON,
                message = "expected '" + expectedValAsString + "', found '" + tActual.toString() + "'");
}
