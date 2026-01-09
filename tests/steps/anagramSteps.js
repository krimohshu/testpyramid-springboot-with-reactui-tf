const { Given, When, Then, Before } = require('@cucumber/cucumber');
const assert = require('assert');
const axios = require('axios');

let input1, input2, response;

Given('the input strings {string} and {string}', function (a, b) {
  input1 = a;
  input2 = b;
});

When('I check if they are anagrams', async function () {
  // call backend directly
  const resp = await axios.post('http://localhost:8080/api/areAnagrams', { input1, input2 });
  response = resp.data;
});

Then('the result should be {string}', function (expected) {
  const expectedBool = expected === 'true';
  assert.strictEqual(response.areAnagrams, expectedBool);
});

