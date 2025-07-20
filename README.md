# MarketPlace
MarketPlace is a plugin for spigot 1.21.4 that allows players to buy & sell items with Vault economy plugins. It uses MongoDB for storage. To set up the plugin, put the jar file in your plugins folder, start the server, and then stop the server. Fill out your MongoDB connection details in config.yml, and then start your server again.

## Commands
### Player Commands
- `/sell <price>`: List the item in your main hand in the marketplace. Requires permission `marketplace.sell`
- `/marketplace`: View all items for sale in the marketplace. Requires permission `marketplace.view`
- `/mylistings`: View your own item listings in the marketplace. Requires permission `marketplace.view`
- `/blackmarket`: View all items for sale in the black market. Items are chosen randomly from the marketplace every 24h, and are discounted by 50%. The seller earns 2x the original price. Requires permission `marketplace.blackmarket`
- `/transactions`: View your transaction history. Requires permission `marketplace.history`
### Admin Commands
- `/marketplace reload`: Reload config.yml. Requires permission `marketplace.reload`
- `/blackmarket refresh`: Instantly refresh the items in the black market. Requires permission `marketplace.refresh`
