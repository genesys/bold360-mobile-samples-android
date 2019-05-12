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
            print(info)
            let infoFile = FileUploadInfo()
            infoFile.fileDescription = "<p><a target='_blank' href='https://www.weightwatchers.com/us/find-a-meeting/'>https://www.weightwatchers.com/us/find-a-meeting/</a></p>"
            self.chatController.handle(BoldEvent.fileUploaded(infoFile))
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
