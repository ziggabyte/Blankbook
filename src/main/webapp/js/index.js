"use strict";

//script för toggle dark mode
const darkModeToggler = document.getElementById("toggle");
const darkStyleLink = document.getElementById("darkStyle");
const darkStylePath = "css/darkStyles.css";

let wholeCookie = document.cookie;
let splitCookies = wholeCookie.split(";");
let hasThemeCookie = splitCookies.length > 1 ? true : false;

function setThemeCookie(value) {
  document.cookie = "theme=" + value;
}

function turnOnDarkTheme() {
  darkStyleLink.setAttribute("href", darkStylePath);
  darkModeToggler.textContent = "Light mode";
}

function turnOffDarkTheme() {
  darkStyleLink.setAttribute("href", " ");
  darkModeToggler.textContent = "Dark mode";
}

if (darkModeToggler) {
  darkModeToggler.addEventListener("click", () => {
    if (darkModeToggler.textContent === "Light mode") {
      turnOffDarkTheme();
      if (hasThemeCookie) setThemeCookie("light");
    } else {
      turnOnDarkTheme();
      if (hasThemeCookie) setThemeCookie("dark");
    }
  });
}

if (hasThemeCookie) {
  if (splitCookies[1].substring(splitCookies[1].indexOf("=") + 1) === "dark") {
    turnOnDarkTheme();
  }
}

// Script för att stänga sökresultaten manuellt
if (document.getElementById("searchResultsContainer")) {
  const searchResultsContainer = document.getElementById(
    "searchResultsContainer"
  );
  const closeButton = document.getElementById("closeSearchResultsButton");
  closeButton.addEventListener("click", () => {
    document.querySelector("main").removeChild(searchResultsContainer);
  });
}
