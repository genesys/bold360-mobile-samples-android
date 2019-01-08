// ===================================================================================================
// Copyright © 2016 bold360ai(LogMeIn).
// BoldAIEngine SDK.
// All rights reserved.
// ===================================================================================================

(function(){
    
    function overrideComponent(container) {
    var fileInput = container.querySelector('input[type="file"]'),
    fileButton = container.querySelector('input[type="button"]'),
    fileList = container.querySelector('.nr-fileuploader-list');
    
    fileInput.parentNode.removeChild(fileInput);
    fileButton.onclick = function() {
    location.href = 'nanorep://contactForm/attachFile';
    }
    
    window.nativeFileAttached = function(fileName) {
    var row = document.createElement('div'),
    item = document.createElement('div'),
    icon = document.createElement('div'),
    file = document.createElement('span'),
    btnRemove = document.createElement('a');
    
    row.className = 'attachmentRow';
    item.className = 'attachment';
    icon.className = 'attachmentIcon';
    btnRemove.className = 'deleteFileLink';
    file.appendChild(document.createTextNode(fileName))
    
    btnRemove.onclick = function() {
    var rowIndex = getRowIndex(row);
    row.parentNode.removeChild(row);
    location.href = 'nanorep://contactForm/removeFile?index=' + rowIndex;
    }
    
    item.appendChild(icon);
    item.appendChild(file);
    item.appendChild(btnRemove);
    row.appendChild(item);
    fileList.appendChild(row);
    }
    
    function getRowIndex(row) {
    var index = -1;
    for (var i = 0; i < fileList.children.length; i++) {
    if (fileList.children[i] === row) {
    index = i;
    break;
    }
    }
    return index;
    }
    }
    
    
    function modifyFormBehavior() {
    // override file inputs
    var fileInputs = document.querySelectorAll('.CF_fileUploadComp');
    for (var i = 0; i < fileInputs.length; i++) {
    overrideComponent(fileInputs[i]);
    }
    
    // override method used in validation
    nanoRep.BundleManager.define({ name: 'MobileSDK', init: function(container) {
                                 var SimpleUploader = container.FileUploader.SimpleUploader
                                 SimpleUploader.prototype.getFileList = function() {
                                 return document.querySelectorAll('.CF_fileUploadComp .attachmentRow');
                                 };
                                 SimpleUploader.prototype.getUploadedFiles = function() {
                                 return [];
                                 }
                                 }});
    }
    
    var formReadyTimer = setInterval(function() {
                                     if (document.querySelector('.CF_fileUploadComp')) {
                                     clearTimeout(formReadyTimer);
                                     modifyFormBehavior();
                                     }
                                     }, 100);
    
    }());



