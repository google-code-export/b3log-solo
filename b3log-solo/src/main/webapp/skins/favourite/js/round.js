addEvent(window, 'load', initCorners);
function initCorners() {
var setting = {
tl: { radius: 6 },
tr: { radius: 6 },
bl: { radius: 6 },
br: { radius: 6 },
antiAlias: true
}
curvyCorners(setting, ".roundedCorners");
}


