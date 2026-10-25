#include <iostream>

#include "arraylist/ArrayList.h"

int main()
{
    ArrayList<int> numbers(1);

    numbers.add(1);
    numbers.add(2);
    numbers.add(3);

    numbers.add(4);
    numbers.add(5);


    std::cout << "NUMBER " << numbers[5];

    numbers.insert(2, 8);

    return 0;
}
