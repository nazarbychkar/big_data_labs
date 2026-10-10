#!/bin/bash

chmod +x mapper.py shuffler.py reducer.py

echo "======================================================"
echo "ТЕСТ 1: Стандартний набір"
echo "======================================================"
{
    echo "apple banana apple" | ./mapper.py
    echo "banana orange apple" | ./mapper.py
} | ./shuffler.py | ./reducer.py
echo ""

echo "======================================================"
echo "ТЕСТ 2: Довгий потік з безліччю повторів"
echo "======================================================"
{
    echo "linux windows macos linux linux" | ./mapper.py
    echo "windows docker kubernetes docker" | ./mapper.py
    echo "linux kubernetes docker linux" | ./mapper.py
    echo "macos linux kubernetes linux" | ./mapper.py

    echo "linux windows macos linux linux" | ./mapper.py
    echo "windows docker kubernetes docker" | ./mapper.py
    echo "linux kubernetes docker linux" | ./mapper.py
    echo "macos linux kubernetes linux" | ./mapper.py
} | ./shuffler.py | ./reducer.py
echo ""

echo "======================================================"
echo "ТЕСТ 3: Унікальні слова"
echo "======================================================"
{
    echo "one two three" | ./mapper.py
    echo "four five six" | ./mapper.py
} | ./shuffler.py | ./reducer.py
echo ""
