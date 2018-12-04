import ballerina/io;

public type Person record {
    string name;
    int age;
    boolean married;
    float salary;
    Address address;
};

public type Address record {
    string country;
    string state;
    string city;
    string street;
};

// returned value is only used for ballerina tests.
public function main() returns (Person, Person, string) {
    Address address = {
        country : "USA",
        state: "NC",
        city: "Raleigh",
        street: "Daniels St"
    };

    Person person = {
        name: "Alex",
        age: 24,
        married: false,
        salary: 8000.0,
        address: address
    };

    var result = person.clone();

    io:println("Source value: ", person);
    io:println("Cloned value: ", result);

    string refCheck = "";
    if (result !== person) {
        refCheck = "Source and Clone are at two different memory locations";
        io:println(refCheck);
    }
    return (person, result, refCheck);
}
