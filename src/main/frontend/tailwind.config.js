/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    extend: {
      backgroundImage: {
        'food-background': "url('/Images/background.jpg')"
      }
    },
  },
  plugins: [],
}



