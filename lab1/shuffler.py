#!/usr/bin/env python3
import sys

def merge_sort(arr):
    if len(arr) <= 1:
        return arr
    
    mid = len(arr) // 2
    left_half = merge_sort(arr[:mid])
    right_half = merge_sort(arr[mid:])
    
    return merge(left_half, right_half)

def merge(left, right):
    sorted_result = []
    i = j = 0
    
    while i < len(left) and j < len(right):
        if left[i] <= right[j]:
            sorted_result.append(left[i])
            i += 1
        else:
            sorted_result.append(right[j])
            j += 1
            
    sorted_result.extend(left[i:])
    sorted_result.extend(right[j:])
    return sorted_result

def main():
    all_lines = []
    
    for line in sys.stdin:
        if line.strip():
            all_lines.append(line.strip())
                
    sorted_lines = merge_sort(all_lines)
    
    for line in sorted_lines:
        print(line)

if __name__ == '__main__':
    main()