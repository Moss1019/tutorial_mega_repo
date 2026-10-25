#pragma once
#include <stdexcept>

template <typename T>
class LinkedList
{
public:
    explicit LinkedList();

    ~LinkedList();

    [[nodiscard]]
    unsigned size() const  { return m_size; }

    T &operator[](unsigned index) const;

    void add(const T &value);

    void remove(unsigned index);

    void insert(unsigned index, const T &value);
private:
    struct Node
    {
        T value;

        Node *next = nullptr;

        Node *previous = nullptr;

        ~Node() { delete next; }
    };

    unsigned m_size;

    Node *m_head;

    Node *m_tail;
};

template<typename T>
LinkedList<T>::LinkedList()
    :m_size(0), m_head(nullptr), m_tail(nullptr)
{

}

template<typename T>
LinkedList<T>::~LinkedList()
{
    delete m_head;
}

template<typename T>
T & LinkedList<T>::operator[](unsigned index) const
{
    if (index >= m_size)
    {
        throw std::out_of_range("index out of range");
    }
    int currentIndex = 0;
    Node *currentNode = m_head;
    while (currentNode && currentIndex < index)
    {
        currentNode = currentNode->next;
        ++currentIndex;
    }
    return currentNode->value;
}

template<typename T>
void LinkedList<T>::add(const T &value)
{
    if (!m_head)
    {
        m_head = new Node();
        m_head->value = value;
        m_tail = m_head;
    }
    else
    {
        Node *newNode = new Node();
        newNode->value = value;
        m_tail->next = newNode;
        newNode->previous = m_tail;
        m_tail = newNode;
    }
    ++m_size;
}

template<typename T>
void LinkedList<T>::remove(unsigned index)
{
    if (index >= m_size)
    {
        throw std::out_of_range("index out of range");
    }
    if (index == 0)
    {
        Node *currentNode = m_head;
        m_head = m_head->next;
        currentNode->next = nullptr;
        delete currentNode;
    }
    else if (index == m_size - 1)
    {
        m_tail = m_tail->previous;
        delete m_tail->next;
    }
    else
    {
        int currentIndex = 0;
        Node *currentNode = m_head;
        while (currentNode && currentIndex < index)
        {
            currentNode = currentNode->next;
            ++currentIndex;
        }
        currentNode->next->previous = currentNode->previous;
        currentNode->previous->next = currentNode->next;
        currentNode->next = nullptr;
        delete currentNode;
    }
    --m_size;
}
//
// (5, ptr) -> (2, ptr) -> (8, ptr) -> (6, ptr) -> nullptr
template<typename T>
void LinkedList<T>::insert(unsigned index, const T &value)
{
    if (index >= m_size)
    {
        throw std::out_of_range("index out of range");
    }
    if (index == 0)
    {
        Node *newNode = new Node();
        newNode->value = value;
        m_head->previous = newNode;
        m_head = newNode;
    }
    else if (index == m_size - 1)
    {
        add(value);
    }
    else
    {
        int currentIndex = 0;
        Node *currentNode = m_head;
        while (currentNode && currentIndex < index)
        {
            currentNode = currentNode->next;
            ++currentIndex;
        }
        Node *newNode = new Node();
        newNode->value = value;
        newNode->next = currentNode;
        newNode->previous = currentNode->previous;
        newNode->previous->next = newNode;
        currentNode->previous = newNode;
    }
    ++m_size;
}


