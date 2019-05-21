// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import Foundation

import Bold360AI

class FileUploadDemoViewController: AgentViewController {
    
    lazy var imagePicker: UIImagePickerController = { return UIImagePickerController()}()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func didClickUploadFile() {
        guard UIImagePickerController.isSourceTypeAvailable(.photoLibrary) else {
            print("can't open photo library")
            return
        }
        self.imagePicker.delegate = self
        self.imagePicker.sourceType = .photoLibrary
        self.navigationController?.presentedViewController?.present(imagePicker, animated: true, completion: nil)
    }
}

extension FileUploadDemoViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {   
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        do {
            picker.dismiss(animated: true)
            if let url = info[.imageURL] as? NSURL, let fileName = url.lastPathComponent, let image = info[.originalImage] as? UIImage, let fileData = image.jpegData(compressionQuality: 1.0) {
                let request = UploadRequest()
                request.fileName = fileName
                request.fileType = .picture
                request.fileData = fileData
                self.chatController.uploadFile(request) { (info: FileUploadInfo!) in
                    self.chatController.handle(BoldEvent.fileUploaded(info))
                }
            }

        }
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        defer {
            picker.dismiss(animated: true)
            let infoFile = FileUploadInfo()
            let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey:"file failed to upload"])
            infoFile.error = error
            self.chatController.handle(BoldEvent.fileUploaded(infoFile))
        }
        
        print("did cancel")
    }
}
