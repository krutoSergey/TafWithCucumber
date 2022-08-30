Feature: Login tests

  Scenario: Successful login with login and password
  Given Open rambler login page
  Then Check that rambler login page is opened
  When Login to rambler with user RAMBLER_STANDARD_USER
  Then Check that user successfully logged in
