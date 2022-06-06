// groups whitespaces to only one, sorts alphabetically and lowercase the string
export const sortKeyWordsAlphabetical = function (string) {
  return string.replace(/\s+/g, ' ').split(' ').sort().join(' ').toLowerCase();
};
