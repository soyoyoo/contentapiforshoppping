/*
 *  Content API for Shopping sample code
 *  This code is modified from the sample code at https://github.com/googleads/googleads-shopping-samples
 *  The Content API for Shopping guide can be found at https://developers.google.com/shopping-content/v2/quickstart
 *  
 *  by JeeWook Kim
 *
 * 	Samples are offered on as-is basis, and designed only to provide you with certain examples of how such code samples could be utilized.
 *  By implementing any of Samples, you agree to solely assume all responsibility for any consequences that arise from such implementation.
 *
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Inventory;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.ShoppingContent.Products.List;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountAdwordsLink;
import com.google.api.services.content.model.AccountUser;
import com.google.api.services.content.model.Error;
import com.google.api.services.content.model.Errors;
import com.google.api.services.content.model.InventoryCustomBatchRequest;
import com.google.api.services.content.model.InventoryCustomBatchRequestEntry;
import com.google.api.services.content.model.InventoryCustomBatchResponse;
import com.google.api.services.content.model.InventoryCustomBatchResponseEntry;
import com.google.api.services.content.model.InventorySetRequest;
import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductStatusDestinationStatus;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.api.services.content.model.ProductsCustomBatchResponse;
import com.google.api.services.content.model.ProductsCustomBatchResponseEntry;
import com.google.api.services.content.model.ProductsListResponse;
import com.google.api.services.content.model.ProductstatusesListResponse;

public class ShoppingSample {
	private static final String FILE_NAME = "MyProject-9ce430751b21.json";
	private static final File KEY_FILE = new File(
			System.getProperty("user.home"), FILE_NAME);
	private static final String APPLICATION_NAME = "My Project-9ce430751b21";
	// Test MCA parent ID (need to change into your account)
	private static BigInteger mcaId = BigInteger.valueOf(111316412);
	// Test Merchant ID with products (need to change into your account)
	private BigInteger merchantId1 = BigInteger.valueOf(112415898);
	// Test Merchant ID to insert/remove products (need to change into your
	// account)
	private BigInteger merchantId2 = BigInteger.valueOf(112427447);
	// Test 3rd party merchant ID (need to change into your account)
	private BigInteger merchantId3 = BigInteger.valueOf(117779752);
	private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	private Credential credential;
	private HttpTransport httpTransport;

	public void execute() throws IOException {
		try {
			credential = GoogleCredential.fromStream(
					new FileInputStream(KEY_FILE)).createScoped(
					ShoppingContentScopes.all());
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			ShoppingContent.Builder builder = new ShoppingContent.Builder(
					httpTransport, jsonFactory, credential)
					.setApplicationName(APPLICATION_NAME);
			ShoppingContent content = builder.build();
		//	listProducts(merchantId2, content);
		//	 listProductstatuses(merchantId2, content);
			 insertProduct(merchantId1,content);
			 insertProduct2(merchantId2,content);
			// updatePrice(merchantId1,content);
			// updateAvailability(merchantId1,content);
			// batchUpdatePriceAvailability(merchantId2,content);
			// batchInsertProducts(merchantId2, content);
			// batchDeleteProducts(merchantId2,content);
			// deleteProduct(merchantId2,content);
			// insertAccount(mcaId, content);
			// deleteAccount(mcaId, BigInteger.valueOf(118467480),content);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void listProducts(BigInteger merchantId, ShoppingContent content) {
		List productsList;
		try {
			productsList = content.products().list(merchantId);

			ProductsListResponse page = productsList.execute();
			while ((page.getResources() != null)
					&& !page.getResources().isEmpty()) {
				for (Product product : page.getResources()) {
					System.out.printf("%s %s %s %s %n", product.getId(),
							product.getTitle(), product.getPrice(),
							product.getAvailability());
				}
				if (page.getNextPageToken() == null) {
					break;
				}
				productsList.setPageToken(page.getNextPageToken());
				page = productsList.execute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listProductstatuses(BigInteger merchantId,
			ShoppingContent content) {

		try {
			ShoppingContent.Productstatuses.List productStatusesList = content
					.productstatuses().list(merchantId);
			ProductstatusesListResponse page = productStatusesList.execute();
			while ((page.getResources() != null)
					&& !page.getResources().isEmpty()) {
				for (ProductStatus productStatus : page.getResources()) {
					System.out.printf("%s %s\n", productStatus.getProductId(),
							productStatus.getTitle());
					for (ProductStatusDestinationStatus status : productStatus
							.getDestinationStatuses()) {
						System.out.printf(" - %s (%s) - %s\n",
								status.getDestination(), status.getIntention(),
								status.getApprovalStatus());
					}
				}
				if (page.getNextPageToken() == null) {
					break;
				}
				productStatusesList.setPageToken(page.getNextPageToken());
				page = productStatusesList.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// insert a new product or update a product if it exists
	public void insertProduct(BigInteger merchantId, ShoppingContent content) {
		try {
			Product product = new Product();

			product.setOfferId("cat01");
			product.setTitle("고양이 장난감");
			product.setDescription("고양이 장난감");
			product.setLink("http://glossy-depot-510.appspot.com/cat/index.html");
			product.setImageLink("http://glossy-depot-510.appspot.com/cat2.png");
			product.setContentLanguage("ko");
			product.setTargetCountry("KR");
			product.setChannel("online");
			product.setAvailability("in stock");
			product.setBrand("브랜드 이름");
			product.setCondition("new");
			product.setGtin("9780009350899");
			product.setAdult(false);
			product.setProductType("카테고리");
			Price price = new Price();
			price.setValue("25000");
			price.setCurrency("KRW");
			product.setPrice(price);

			Product result = content.products().insert(merchantId, product)
					.execute();

			System.out.println("product insert executed");

			if (result != null) {
				System.out.printf("Product %s inserted%n", result.getOfferId());
				java.util.List<Error> warnings = result.getWarnings();
				if (warnings != null) {
					System.out.printf("There are %d warnings%n",
							warnings.size());

					for (Error warning : warnings) {
						System.out.printf("[%s] %s%n", warning.getReason(),
								warning.getMessage());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// insert a new product or update a product if it exists
	public void insertProduct2(BigInteger merchantId, ShoppingContent content) {
		try {
			Product product = new Product();

			product.setOfferId("cat01");
			product.setTitle("Cat Toy");
			product.setDescription("Cat Toy");
			product.setLink("http://glossy-depot-510.appspot.com/cat/index.html");
			product.setImageLink("http://glossy-depot-510.appspot.com/cat2.png");
			product.setContentLanguage("en");
			product.setTargetCountry("TW");
			product.setChannel("online");
			product.setAvailability("in stock");
			product.setBrand("brand");
			product.setCondition("new");
			product.setGtin("9780009350899");
			product.setAdult(false);
			product.setProductType("cat1");
			Price price = new Price();
			price.setValue("25000");
			price.setCurrency("TWD");
			product.setPrice(price);

			Product result = content.products().insert(merchantId, product)
					.execute();

			System.out.println("product insert executed");

			if (result != null) {
				System.out.printf("Product %s inserted%n", result.getOfferId());
				java.util.List<Error> warnings = result.getWarnings();
				if (warnings != null) {
					System.out.printf("There are %d warnings%n",
							warnings.size());

					for (Error warning : warnings) {
						System.out.printf("[%s] %s%n", warning.getReason(),
								warning.getMessage());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatePrice(BigInteger merchantId, ShoppingContent content) {
		try {
			String id = "online:ko:KR:cat01";
			InventorySetRequest inventory = new InventorySetRequest();

			Price price = new Price();
			price.setValue("13000");
			price.setCurrency("KRW");

			inventory.setPrice(price);

			content.inventory().set(merchantId, "online", id, inventory)
					.execute();
			System.out.printf("Update price executed with product ID %s%n", id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateAvailability(BigInteger merchantId,
			ShoppingContent content) {
		try {
			String id = "online:ko:KR:cat01";
			InventorySetRequest inventory = new InventorySetRequest();
			inventory.setAvailability("out of stock");
			content.inventory().set(merchantId, "online", id, inventory)
					.execute();
			System.out.printf(
					"Update availability executed with product ID %s%n", id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteProduct(BigInteger merchantId, ShoppingContent content) {
		try {
			String id = "online:ko:KR:book1";
			content.products().delete(merchantId, id).execute();
			System.out.printf("Delete product executed with product ID %s%n",
					id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void batchInsertProducts(BigInteger merchantId,
			ShoppingContent content) {
		try {
			java.util.List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries = new ArrayList<ProductsCustomBatchRequestEntry>();
			ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
			for (int i = 1; i <= 100; i++) {
				Product product = new Product();
				product.setOfferId("cat" + i);
				product.setTitle("고양이 장난감 " + i);
				product.setDescription("고양이 장난감 " + i);
				product.setLink("http://glossy-depot-510.appspot.com/cat/index.html");
				product.setImageLink("http://glossy-depot-510.appspot.com/cat2.png");
				product.setContentLanguage("ko");
				product.setTargetCountry("KR");
				product.setChannel("online");
				product.setAvailability("in stock");
				product.setCondition("new");
				product.setGtin("978000935089" + i);
				product.setAdult(false);

				Price price = new Price();
				price.setValue("12000");
				price.setCurrency("KRW");
				product.setPrice(price);

				ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
				entry.setBatchId((long) i);
				entry.setMerchantId(merchantId);
				entry.setProduct(product);
				entry.setMethod("insert");
				productsBatchRequestEntries.add(entry);
			}

			batchRequest.setEntries(productsBatchRequestEntries);
			ProductsCustomBatchResponse batchResponse = content.products()
					.custombatch(batchRequest).execute();

			for (ProductsCustomBatchResponseEntry entry : batchResponse
					.getEntries()) {
				Product product = entry.getProduct();
				System.out.printf("Inserted %s%n", product.getOfferId());
				java.util.List<Error> warnings = product.getWarnings();
				if (warnings != null) {
					System.out.printf("There are %d warnings%n",
							warnings.size());

					for (Error warning : warnings) {
						System.out.printf("[%s] %s%n", warning.getReason(),
								warning.getMessage());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void batchDeleteProducts(BigInteger merchantId,
			ShoppingContent content) {
		try {
			java.util.List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries = new ArrayList<ProductsCustomBatchRequestEntry>();
			ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
			for (int i = 1; i <= 100; i++) {
				ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
				entry.setBatchId((long) i);
				entry.setMerchantId(merchantId);
				entry.setProductId("online:ko:KR:book" + i);
				entry.setMethod("delete");
				productsBatchRequestEntries.add(entry);
			}

			batchRequest.setEntries(productsBatchRequestEntries);
			ProductsCustomBatchResponse batchResponse = content.products()
					.custombatch(batchRequest).execute();

			for (ProductsCustomBatchResponseEntry entry : batchResponse
					.getEntries()) {
				System.out
						.println("batch " + entry.getBatchId() + " requested");
				Errors errors = entry.getErrors();
				java.util.List<Error> list = null;
				if (errors != null)
					list = errors.getErrors();
				if (list != null) {
					System.out.printf("batch id %s has errors",
							entry.getBatchId());
					for (Error error : list) {
						System.out.printf("[%s] %s%n", error.getReason(),
								error.getMessage());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void batchUpdatePriceAvailability(BigInteger merchantId,
			ShoppingContent content) {
		try {
			java.util.List<InventoryCustomBatchRequestEntry> inventoryBatchRequestEntries = new ArrayList<InventoryCustomBatchRequestEntry>();
			InventoryCustomBatchRequest batchRequest = new InventoryCustomBatchRequest();
			for (int i = 1; i <= 100; i++) {
				Price price = new Price();
				price.setValue("55000");
				price.setCurrency("KRW");

				Inventory inventory = new Inventory();
				inventory.setPrice(price);
				inventory.setAvailability("out of stock");

				InventoryCustomBatchRequestEntry entry = new InventoryCustomBatchRequestEntry();
				entry.setBatchId((long) i);
				entry.setMerchantId(merchantId);
				entry.setInventory(inventory);
				entry.setProductId("online:ko:KR:cat" + i);
				entry.setStoreCode("online");
				inventoryBatchRequestEntries.add(entry);
			}

			batchRequest.setEntries(inventoryBatchRequestEntries);
			InventoryCustomBatchResponse batchResponse = content.inventory()
					.custombatch(batchRequest).execute();

			for (InventoryCustomBatchResponseEntry entry : batchResponse
					.getEntries()) {
				System.out.printf("update requested with batch id %s%n",
						entry.getBatchId());
				Errors errors = entry.getErrors();
				java.util.List<Error> list = null;
				if (errors != null)
					list = errors.getErrors();
				if (list != null) {
					System.out.printf("batch id %s has errors",
							entry.getBatchId());
					for (Error error : list) {
						System.out.printf("[%s] %s%n", error.getReason(),
								error.getMessage());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertAccount(BigInteger merchantId, ShoppingContent content) {
		try {
			Account account = new Account();
			account.setName("subaccount12");
			account.setWebsiteUrl("http://my-book-shop.com/sub12");
			AccountUser user = new AccountUser();
			user.setAdmin(false);
			user.setEmailAddress("no.reply.ga.audit@gmail.com");
			java.util.List<AccountUser> users = Arrays.asList(user);

			account.setUsers(users);
			System.out.println("Inserting new account.");

			Account result = content.accounts().insert(mcaId, account)
					.execute();
			printAccount(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteAccount(BigInteger mcaId, BigInteger merchantId,
			ShoppingContent content) {
		try {
			System.out.printf("Deleting account with ID %d%n", merchantId);
			content.accounts().delete(mcaId, merchantId).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printAccount(Account account) {
		System.out.printf("Information for account %d:%n", account.getId());
		if (account.getName() == null) {
			System.out.println("- No display name found.");
		} else {
			System.out.printf("- Display name: %s%n", account.getName());
		}
		if (account.getWebsiteUrl() == null) {
			System.out.println("- No website URL information found.");
		} else {
			System.out.printf("- Website URL: %s%n", account.getWebsiteUrl());
		}
		if (account.getUsers() == null) {
			System.out
					.println("- No registered users for this Merchant Center account.");
		} else {
			System.out.println("- Registered users:");
			for (AccountUser user : account.getUsers()) {
				System.out.printf("  - %s%s%n", user.getAdmin() ? "(ADMIN) "
						: "", user.getEmailAddress());
			}
		}
		if (account.getAdwordsLinks() == null) {
			System.out
					.println("- No links to AdWords accounts for this Merchant Center account.");
		} else {
			System.out.println("- Links to AdWords accounts:");
			for (AccountAdwordsLink link : account.getAdwordsLinks()) {
				System.out.printf("  - %d: %s%n", link.getAdwordsId(),
						link.getStatus());
			}
		}
		System.out.println();
	}

	public static void main(String[] args) {
		try {
			new ShoppingSample().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
