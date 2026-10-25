
#include <cassert>
#include <iostream>

#include "../arraylist/ArrayList.h"
#include "../linkedlist/LinkedList.h"

static void testArrayList()
{
    ArrayList<int> numbers(1);

    numbers.add(1);
    numbers.add(2);
    numbers.add(3);

    assert(numbers[0] == 1);
    assert(numbers[1] == 2);
    assert(numbers[2] == 3);

    numbers.add(4);
    numbers.add(5);

    assert(numbers.size() == 5);

    numbers.insert(2, 8);

    assert(numbers.size() == 6);

    assert(numbers[0] == 1);
    assert(numbers[1] == 2);
    assert(numbers[2] == 8);
    assert(numbers[3] == 3);
    assert(numbers[4] == 4);
    assert(numbers[5] == 5);

    numbers.remove(3);

    assert(numbers.size() == 5);

    assert(numbers[0] == 1);
    assert(numbers[1] == 2);
    assert(numbers[2] == 8);
    assert(numbers[3] == 4);
    assert(numbers[4] == 5);

}

static void testLinkedList()
{
    LinkedList<int> numbers;

    numbers.add(1);
    numbers.add(2);
    numbers.add(3);

    assert(numbers[0] == 1);
    assert(numbers[1] == 2);
    assert(numbers[2] == 3);

    numbers.add(4);
    numbers.add(5);

    assert(numbers.size() == 5);

    numbers.insert(2, 8);

    assert(numbers.size() == 6);

    assert(numbers[0] == 1);
    assert(numbers[1] == 2);
    assert(numbers[2] == 8);
    assert(numbers[3] == 3);
    assert(numbers[4] == 4);
    assert(numbers[5] == 5);

    numbers.remove(0);

    assert(numbers.size() == 5);

    assert(numbers[0] == 2);
    assert(numbers[1] == 8);
    assert(numbers[2] == 3);
    assert(numbers[3] == 4);
    assert(numbers[4] == 5);
}

int main()
{
    testArrayList();
    testLinkedList();
    return 0;
}
