// ===================================================================================================
// Copyright © 2019 bold360ai(LogMeIn).
// Bold360AI SDK.
// All rights reserved.
// ===================================================================================================

import Foundation
import CoreData


extension ChatConfiguration {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<ChatConfiguration> {
        return NSFetchRequest<ChatConfiguration>(entityName: "ChatConfiguration")
    }

    @NSManaged public var avatarData: NSData?
    @NSManaged public var position: Int16
    @NSManaged public var textColorHex: String?
    @NSManaged public var historyItem: ChatHistoryItem?

}
