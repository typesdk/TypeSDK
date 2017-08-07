module.exports = function (path) {
  try {
    return decodeURIComponent(path);
  } catch (e) {
    return -1
  }
}
