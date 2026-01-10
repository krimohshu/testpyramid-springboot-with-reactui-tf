Feature: Anagram Checker
  As a user I want to check if two strings are anagrams

  Scenario Outline: Check if two strings are anagrams
    Given the input strings "<input1>" and "<input2>"
    When I check if they are anagrams
    Then the result should be "<output>"

    Examples:
      | input1        | input2          | output |
      | listen        | silent          | true   |
      | hello         | world           | false  |
      | conversation  | voices rant on  | true   |
      | school master | the classroom   | true   |

