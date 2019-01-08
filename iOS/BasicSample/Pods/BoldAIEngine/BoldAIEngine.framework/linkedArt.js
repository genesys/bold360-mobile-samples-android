// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

<style>
img {
    max-width: 100% !important;
height: auto !important;
}

body {
    font-family: Roboto-Regular;
    color: #6c6c6c;
}

table
{
    table-layout: fixed;
    max-width: none;
    width: auto;
    min-width: 100%;
    border: solid thin;
}

td {
    border: solid thin;
}

tbody {
    border: solid thin;
}

</style>
<script>
(function() {
 var embeds = document.querySelectorAll('iframe');
 for (var i = 0, embed, content, width, height, ratio, wrapper; i < embeds.length; i++) {
 embed = embeds[i];
 width = embed.getAttribute('width'),
 height = embed.getAttribute('height')
 ratio = width / height;

 // skip frames with relative dimensions
 if (isNaN(ratio)) continue;

 // set wrapper styles
 wrapper = document.createElement('div');
 wrapper.style.position = 'relative';
 wrapper.style.width = width.indexOf('%') < 0 ? parseFloat(width) + 'px' : width;
 wrapper.style.maxWidth = '100%';

 // set content styles
 content = document.createElement('div');
 content.style.paddingBottom = 100 / ratio + '%';

 // set embed styles
 embed.style.position = 'absolute';
 embed.style.width = '100%';
 embed.style.height = '100%';

 // update DOM structure
 embed.parentNode.insertBefore(wrapper, embed);
 content.appendChild(embed);
 wrapper.appendChild(content);
 }
 }());
</script>
<script>
var links = document.querySelectorAll("a[nanorepLinkId]");

for (var i = 0; i < links.length; i++) {
var link = links[i];

var id = link.getAttribute('nanorepLinkId');
link.href = "nanorep://id/"+ id;
}
</script>
