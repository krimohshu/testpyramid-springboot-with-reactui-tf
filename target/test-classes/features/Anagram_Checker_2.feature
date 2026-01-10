Feature: Anagram Checker - Part 2
  As a user I want to check if two strings are anagrams

  Scenario Outline: Check if two strings are anagrams
    Given the input strings "<input1>" and "<input2>"
    When I check if they are anagrams
    Then the result should be "<output>"

    Examples:
      | input1            | input2           | output |
      | a gentleman       | elegant man      | true   |
      | eleven plus two   | twelve plus one  | true   |
      | apple             | paple            | true   |
      | rat               | car              | false  |

