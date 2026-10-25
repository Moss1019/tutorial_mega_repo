#pragma once
#include <stdexcept>

template <typename T>
class ArrayList
{
public:
    explicit ArrayList(unsigned initCapacity);

    ~ArrayList();

    [[nodiscard]]
    unsigned size() const  { return m_size; }

    T &operator[](unsigned index) const;

    void add(const T &value);

    void remove(unsigned index);

    void insert(unsigned index, const T &value);

private:
    unsigned m_capacity;

    unsigned m_size;

    char *m_buffer;

    void reallocate(unsigned newCapacity);
};

template<typename T>
ArrayList<T>::ArrayList(unsigned initCapacity)
    :m_capacity(initCapacity), m_size(0), m_buffer(nullptr)
{
    reallocate(initCapacity);
}

template<typename T>
ArrayList<T>::~ArrayList()
{
    delete[] m_buffer;
}

template<typename T>
T &ArrayList<T>::operator[](unsigned index) const
{
    T *buffer = reinterpret_cast<T *>(m_buffer);
    return buffer[index];
}

template<typename T>
void ArrayList<T>::add(const T &value)
{
    if (m_size == m_capacity)
    {
        reallocate(m_capacity * 2);
    }
    T *buffer = reinterpret_cast<T *>(m_buffer);
    buffer[m_size] = value;
    ++m_size;
}

template<typename T>
void ArrayList<T>::remove(unsigned index)
{
    if (index >= m_size)
    {
        throw std::out_of_range("index out of range");
    }
    T *buffer = reinterpret_cast<T *>(m_buffer);
    for (unsigned i = index; i < m_size - 1; ++i)
    {
        buffer[i] = buffer[i + 1];
    }
    --m_size;
}

// [1 4 3 2]
// [1 4 3 2 _ _ _ _ ]
// [1 4 8 3 2 _ _ _ ]
template<typename T>
void ArrayList<T>::insert(unsigned index, const T &value)
{
    if (index >= m_size)
    {
        throw std::out_of_range("index out of range");
    }
    if (m_size == m_capacity)
    {
        reallocate(m_capacity * 2);
    }
    T *buffer = reinterpret_cast<T *>(m_buffer);
    for (unsigned i = m_size; i > index; --i)
    {
        buffer[i] = buffer[i - 1];
    }
    buffer[index] = value;
    ++m_size;
}

template<typename T>
void ArrayList<T>::reallocate(unsigned newCapacity)
{
    m_capacity = newCapacity;
    char *buffer = new char[newCapacity * sizeof(T)];
    if (m_buffer)
    {
        T *oldBuffer = reinterpret_cast<T *>(m_buffer);
        T *newBuffer = reinterpret_cast<T *>(buffer);
        for (unsigned i = 0; i < m_size; ++i)
        {
            newBuffer[i] = oldBuffer[i];
        }
        delete[] m_buffer;
    }
    m_buffer = buffer;
}
